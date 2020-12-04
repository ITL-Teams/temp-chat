function broadcastService(stream, chanel, message) {
  const clients = stream.getWss().clients;
  clients.forEach((client) => {
    if (client.channel === chanel) client.send(message);
  });
}

function aliveConnections(stream) {
  const clients = stream.getWss().clients;
  let aliveConnections = 0;
  clients.forEach((client) => {
    aliveConnections++;
  });
  return aliveConnections;
}

function disconnectIfDuplicate(ws, stream, limit) {
  const MAX_CONNECTION_LIMIT = limit;
  const clients = stream.getWss().clients;
  let duplicatedConnections = [];

  clients.forEach((client) => {
    if (client.address === ws.address) duplicatedConnections.push(client);
  });

  let aliveConnections = duplicatedConnections.length;
  for (connection of duplicatedConnections) {
    if (aliveConnections <= MAX_CONNECTION_LIMIT) break;

    connection.terminate();
    aliveConnections--;
  }
}

const LOG_STATUS = {
  OPEN: "open",
  CLOSE: "closed",
};
function log(ws, stream, status) {
  console.log(`${ws.clientId} ${status}, alive: ${aliveConnections(stream)}`);
}

exports.broadcastService = broadcastService;
exports.aliveConnections = aliveConnections;
exports.disconnectIfDuplicate = disconnectIfDuplicate;
exports.log = log;
exports.LOG_STATUS = LOG_STATUS;
