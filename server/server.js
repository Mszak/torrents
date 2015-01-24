var net = require('net'),
  File = require('./models/File'),
  Seeder = require('./models/Seeder'),
  _ = require('underscore'),


  server,
  sockets = {},
  socketId = 7,
  files = [],
  fileHashes = {},
  fileId = 0,
  getSocketKey, getUserId, registerPeer, getList;


getSocketKey = function (socket) {
  return socket.remoteAddress;

  // Storing client port only works with an open socket
  // return socket.remoteAddress + ':' + socket.remotePort;
};

getUserId = function (socket) {
  return sockets[getSocketKey(socket)];
};

registerPeer = function (socket) {
  var key = getSocketKey(socket);
  if (typeof sockets[key] === 'undefined') {
    console.log('assigning id', socketId)
    sockets[key] = socketId++;
  }
  return sockets[key];
};

getList = function () {
  return files.map(function (file) {
    return file.name + ':' + file.id;
  }).join(',');
};

server = net.createServer(function (socket) {
  var validateUserId, putFile, tickFile, getFileSeeders;

  validateUserId = function (userId) {
    return getUserId(socket) == userId;
  },
  putFile = function(userId, fileName, chunksCount, fileHash) {
    var ip = socket.remoteAddress,
        port = socket.remotePort,
        file,
        seeder;

    if (!validateUserId(userId)) {
      console.log('VALIDATION ERROR:', socket.remoteAddress, socket.remotePort, getUserId(socket), userId)
      return 'ERROR: INVALID USER_ID\n';
    }

    if (typeof fileHashes[fileHash] === 'undefined') {
      file = new File(fileId++, fileName, chunksCount, fileHash);
      files[file.id] = file;
      fileHashes[fileHash] = file.id;
    } else {
      file = files[fileHashes[fileHash]];
    }

    seeder = new Seeder(socket.remoteAddress, 10000, Date.now(), userId);
    file.registerSeeder(seeder);

    console.log('putFile', seeder, file.getSeeders())

    return file.id + '\n';
  },
  tickFile = function (userId, fileId) {
    if (!validateUserId(userId)) return false;

    var file = files[fileId];

    if (file) {
      file.tickSeeder(userId);
    }
  },
  getFileSeeders = function (userId, fileId) {
    if (!validateUserId(userId)) return false;

    var result = '', file = files[fileId];

    result += file.getSeeders().join(',') + '\n';
    result += file.name + ',' + file.chunksCount + '\n';

    seeder = new Seeder(socket.remoteAddress, 10000, Date.now(), userId);
    file.registerSeeder(seeder);

    return result;
  };

  socket.on('data', function (data){
    var raw = data.toString().split(" ");
    var command = raw[0];

    switch (command) {
      case ("REGISTER") :
        socket.end(registerPeer(socket) + '\n');
        break;
      case ("LIST") :
        socket.end(getList() + "\n");
        break;
      case ("GET") :
        socket.end(getFileSeeders(raw[1], raw[2]));
        break;
      case ("PUT") :
        socket.end(putFile(raw[1], raw[2], raw[3], raw[4]));
        break;
      case ("TICK") :
        tickFile(raw[1], raw[2]);
        socket.end();
        break;
      default :
        socket.end('UNACCEPTABLE COMMAND\n');
    }
  });

  socket.on('error', function (err) {
    console.log('SOCKET ERROR:', err);
  });
});

setInterval(function () {
  files.forEach(function (file) {
    file.removeDisconnectedSeeders();
  });
}, 10 * 1000);

server.listen(3003);
