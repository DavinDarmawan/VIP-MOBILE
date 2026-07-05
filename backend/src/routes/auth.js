const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('../db');

const router = express.Router();
const JWT_SECRET = process.env.BACKEND_JWT_SECRET || 'change_this_secret';

router.post('/register', async (req, res) => {
  try {
    const { full_name, phone_number, password } = req.body;
    if (!full_name || !phone_number || !password) return res.status(400).json({ error: 'Missing fields' });

    const [exists] = await pool.query('SELECT user_id FROM users WHERE phone_number = ?', [phone_number]);
    if (exists.length) return res.status(400).json({ error: 'Phone number exists' });

    const password_hash = await bcrypt.hash(password, 10);
    const [result] = await pool.query(
      'INSERT INTO users (full_name, phone_number, password_hash, status, kyc_status) VALUES (?, ?, ?, ?, ?)',
      [full_name, phone_number, password_hash, 'active', 'unverified']
    );

    const userId = result.insertId;
    const token = jwt.sign({ user_id: userId, phone_number }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ user_id: userId, access_token: token });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Server error' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { phone_number, password } = req.body;
    if (!phone_number || !password) return res.status(400).json({ error: 'Missing fields' });

    const [rows] = await pool.query('SELECT user_id, password_hash FROM users WHERE phone_number = ?', [phone_number]);
    if (!rows.length) return res.status(401).json({ error: 'Invalid credentials' });

    const user = rows[0];
    const ok = await bcrypt.compare(password, user.password_hash);
    if (!ok) return res.status(401).json({ error: 'Invalid credentials' });

    const token = jwt.sign({ user_id: user.user_id, phone_number }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ user_id: user.user_id, access_token: token });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;
