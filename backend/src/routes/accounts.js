const express = require('express');
const pool = require('../db');
const router = express.Router();

// Simple endpoint to get transactions for an account
router.get('/:accountId/transactions', async (req, res) => {
  try {
    const accountId = req.params.accountId;
    const [rows] = await pool.query('SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT 100', [accountId]);
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;

