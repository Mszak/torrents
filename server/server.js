var net = require('net');
var FileObject = require('./models/File');
var PeerObject = require('./models/Peer');
var ConnectionObject = require('./models/Connection');


var FileArray = [];
var PeerArray = [];
var ConnectionArray = [];
var connectionIndex = 7;
var fileIndex = 0;

var server = net.createServer(function (socket) {

    socket.on('data', function(data){
        var raw = data.toString().split(" ");
        var command = raw[0];

        switch (command) {
            case ("REGISTER") :
                break;
            case ("LIST") :
                socket.write(getList());
                break;
            case ("GET") :
                socket.write(getFilePeers(raw[1], raw[2]));
                break;
            case ("PUT") :
                socket.write(putFile(raw[1], raw[2], raw[3]));
                break;
            case ("TICK") :
                tickFile(raw[1]);
                break;
            default :
                socket.write('UNACCEPTABLE COMMAND\n');
        }
    });

    socket.on('close', function(data){
       console.log('disconnected');
    });

    socket.on('error', function(data){
       console.log('error');
    });

    var getList = function () {
        var result = "";
        var tmp;
        for (var i = 0; i < FileArray.length; i++) {
            if(i == 0)
                tmp =  FileArray[i].name + "," + FileArray[i].id
            else
                tmp = ":" + FileArray[i].name + "," + FileArray[i].id
            result += tmp;
        }
        result += "\n";
        return result ;
    };

    var getFilePeers = function (userId, id) {
        var result = "";
        var tmp;
        for (var i = 0; i < PeerArray.length; i++) {
            if(PeerArray[i].file_id == id) {
                if(result == "")
                    tmp = PeerArray[i].ip + "," + PeerArray[i].port
                else
                    tmp = ":" + PeerArray[i].ip + "," + PeerArray[i].port

                result += tmp;
            }
        }
        result += "\n";
        for (var i = 0; i < FileArray.length; i++) {
            if (FileArray[i].id == id) {
                tmp = FileArray[i].name + "," + FileArray[i].chunks + "\n";
                result += tmp;
                break;
            }
        }

        return result;
    }

    var putFile = function(user_id, filename, chunks) {
        var user_ip, port;
        for (var i = 0; i < ConnectionArray.length; i++) {
            if (ConnectionArray[i].id == user_id) {
                user_ip = ConnectionArray[i].user_ip;
                port = ConnectionArray[i].port;
            }
        }
        for (var i = 0; i < FileArray.length; i++) {
            if (FileArray[i].name == filename) {
                var peer = new PeerObject(user_ip, port, FileArray[i].id);
                return FileArray[i].id;
            }
        }
        var file = new FileObject(fileIndex++, filename, chunks);
        var peer = new PeerObject(user_ip, port, file.id);
        FileArray.push(file);
        PeerArray.push(peer);
        return file.id.toString() + "\n";
    }

    var tickFile = function (id) {
        return id;
    }
});

server.on('connection', function(socket){
    for(var i = 0; i < ConnectionArray.length; i++) {
        if(ConnectionArray[i].user_ip == socket.remoteAddress)
            return;
    }
    var connection = new ConnectionObject(connectionIndex++, socket.remoteAddress, 10000)
    //to do change port (socket.remotePort)
    ConnectionArray.push(connection);
    socket.write(connection.id.toString() + "\n");
});


server.listen(3001);