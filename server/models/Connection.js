module.exports = function Connection(id, user_ip, port){
    this.user_ip = user_ip;
    this.port = port
    this.id = id;
};