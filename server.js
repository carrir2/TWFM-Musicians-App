const express = require('express');
const app = express();
const port = 3000;
const path = require('path');
const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

app.use(express.static(path.join(__dirname, '/musician-application-solution/dist/musician-application-solution')));
app.listen(port, () => {
	console.log('Listening on *:3000');
});


app.post('/testDelivery', (req, res) => {
	console.log(req.body);
	res.json(req.body);
});