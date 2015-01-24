var _ = require('underscore')

module.exports = function File (id, name, chunksCount, hash){
  var seeders = {};

  this.id = id;
  this.name = name;
  this.chunksCount = chunksCount;
  this.hash = hash;

  this.registerSeeder = function (seeder) {
    seeders[parseInt(seeder.userId)] = seeder;
  };

  this.tickSeeder = function (userId) {
    userId = parseInt(userId);
    seeders[userId].tickTime = Date.now();
  }

  this.removeDisconnectedSeeders = function () {
    var now = Date.now();
    for (var userId in seeders) {
      if (!seeders.hasOwnProperty(userId))
        return;

      if (seeders[parseInt(userId)].tickTime + 7 * 1000 < now)
        delete seeders[userId];
    }
  }

  this.getSeeders = function () {
    return _.map(seeders, function (seeder) {
      return seeder.getAddress();
    });
  }
};
