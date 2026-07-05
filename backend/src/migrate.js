const fs = require('fs');
const path = require('path');
const pool = require('./db');

module.exports = async function runMigrations() {
  const migrationsDir = path.resolve(__dirname, '../migrations');
  if (!fs.existsSync(migrationsDir)) return;

  const client = await pool.getConnection();
  try {
    await client.query(
      `CREATE TABLE IF NOT EXISTS schema_migrations (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)`
    );
    // If the database already contains user tables (initialized via docker init),
    // assume schema is already applied and mark migrations as applied to avoid errors.
    const [tables] = await client.query('SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? LIMIT 1', [process.env.MYSQL_DATABASE || process.env.DB_NAME || 'ibs_core']);
    const files = fs.readdirSync(migrationsDir).filter((f) => f.endsWith('.sql')).sort();

    if (tables && tables.length > 0) {
      console.log('Database already contains tables; marking existing migration files as applied.');
      for (const file of files) {
        const [rows] = await client.query('SELECT name FROM schema_migrations WHERE name = ?', [file]);
        if (!rows.length) {
          await client.query('INSERT INTO schema_migrations (name) VALUES (?)', [file]);
          console.log(`Marked migration as applied: ${file}`);
        }
      }
      return;
    }

    for (const file of files) {
      const [rows] = await client.query('SELECT name FROM schema_migrations WHERE name = ?', [file]);
      if (rows.length) {
        console.log(`Skipping already applied migration: ${file}`);
        continue;
      }
      const sql = fs.readFileSync(path.join(migrationsDir, file), 'utf8');
      console.log(`Applying migration: ${file}`);
      await client.query(sql);
      await client.query('INSERT INTO schema_migrations (name) VALUES (?)', [file]);
    }
  } finally {
    client.release();
  }
};


