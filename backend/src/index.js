const express = require('express');
const dotenv = require('dotenv');
const authRoutes = require('./routes/auth');
const accountsRoutes = require('./routes/accounts');

dotenv.config();
const app = express();
app.use(express.json());

app.get('/health', (req, res) => res.json({ status: 'ok' }));
app.use('/api/auth', authRoutes);
app.use('/api/accounts', accountsRoutes);

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`Backend listening on port ${port}`));

