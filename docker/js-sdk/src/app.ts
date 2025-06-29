import express, {Request, Response} from 'express';
import {OpenFgaClient} from '@openfga/sdk';

const app = express();
app.use(express.json());

const apiUrl = 'http://openfga:8080';
const client = new OpenFgaClient({apiUrl});

app.get('/health', (req: Request, res: Response) => {
    res.sendStatus(200);
});

app.post('/stores', async (req: Request, res: Response) => {
    const response = await client.createStore(req.body);
    res.status(response.$response.status).json(response);
});

const PORT = 9000;
app.listen(PORT, () => console.log(`OpenFGA SDK wrapper running on :${PORT}`));
