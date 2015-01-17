var net = require('net');

var HOST = '83.22.65.18';
var PORT = 3000;

var client = new net.Socket();
client.connect(PORT, HOST, function(){
  console.log('connected');
  client.write('test');
});

client.on('data', function(data){
  console.log('data:' + data);
});

