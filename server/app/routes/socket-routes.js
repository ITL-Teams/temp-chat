const router = require("express").Router();
const uuid = require("uuid").v4;
const SocketUtils = require("../service/SocketUtils");
const MessageValidator = require("../service/MessageValidator");
const DateFormatter = require("../service/DateFormatter");

let stream = null;
const CLIENT_CONNECTIONS_LIMIT = 5;

router.ws("/chat/:channel", function (ws, req) {
  ws.clientId = uuid();
  ws.channel = req.params.channel;
  ws.address = req.socket.remoteAddress;

  SocketUtils.disconnectIfDuplicate(ws, stream, CLIENT_CONNECTIONS_LIMIT);
  SocketUtils.log(ws, stream, SocketUtils.LOG_STATUS.OPEN);
  ws.send(ws.clientId);

  ws.on("message", function (message) {
    const validator = MessageValidator.validate(message);

    if (!validator.hasError) {
      current_time = new Date();
      message = JSON.parse(message);
      message.user_id = ws.clientId;
      message.date = DateFormatter.getDateFormatted(current_time);
      message.time = DateFormatter.getTimeFormatted(current_time);
      message = JSON.stringify(message);
      SocketUtils.broadcastService(stream, ws.channel, message);
    } else ws.send(JSON.stringify(validator));
  });

  ws.on("close", () =>
    SocketUtils.log(ws, stream, SocketUtils.LOG_STATUS.CLOSE)
  );
});

function getRouter(expressWs) {
  stream = expressWs;
  return router;
}

module.exports = getRouter;
