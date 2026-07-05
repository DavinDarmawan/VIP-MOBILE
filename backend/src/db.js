const mysql = require('mysql2/promise');
require('dotenv').config();

const pool = mysql.createPool({
  host: process.env.DB_HOST || 'ibs-db',
  user: process.env.MYSQL_USER || process.env.DB_USER || 'ibs_user',
  password: process.env.MYSQL_PASSWORD || process.env.DB_PASSWORD || 'ibs_pass',
  database: process.env.MYSQL_DATABASE || process.env.DB_NAME || 'ibs_core',
  waitForConnections: true,
  connectionLimit: 10,
});

module.exports = pool;

