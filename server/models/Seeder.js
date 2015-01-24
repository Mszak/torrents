module.exports = function Seeder(ip, port, tickTime, userId){
    this.ip = ip;
    this.port = port;
    this.tickTime = tickTime;
    this.userId = userId;

    this.getAddress = function () {
      return this.ip + ':' + this.port;
    }
};
