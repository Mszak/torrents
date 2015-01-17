module.exports = function Peer(ip, port, fileId, tickTime, userId){
    this.ip = ip;
    this.port = port;
    this.fileId = fileId;
    this.tickTime = tickTime;
    this.userId = userId;
};