const pool = require('./db');

async function waitForDb(retries = 30, delayMs = 2000) {
  for (let i = 0; i < retries; i++) {
    try {
      const conn = await pool.getConnection();
      conn.release();
      console.log('Database is ready');
      return;
    } catch (err) {
      console.log(`Waiting for database... (${i + 1}/${retries})`);
      await new Promise((r) => setTimeout(r, delayMs));
    }
  }
  throw new Error('Could not connect to database');
}

(async () => {
  try {
    await waitForDb();
    // Run migrations if any
    try {
      const migrate = require('./migrate');
      await migrate();
    } catch (mErr) {
      console.warn('Migration step failed or not present:', mErr.message || mErr);
    }

    require('./index');
  } catch (err) {
    console.error('Failed to start application:', err);
    process.exit(1);
  }
})();

