function isNull(message) {
  return message === undefined || message === null;
}

function isValidJson(json) {
  try {
    JSON.parse(json);
    return true;
  } catch (e) {
    return false;
  }
}

function hasUserName(message) {
  return typeof message.username === "string";
}

function hasContent(message) {
  return typeof message.content === "string";
}

function hasDeleteFlag(message) {
  return typeof message.delete === "boolean";
}

function isADeleteMessage(message) {
  return message.delete;
}

let validatorReponse = {
  hasError: true,
  channel: "",
  message: "not validated yet",
};

function getError(message) {
  if (isNull(message)) return "message is null";
  if (!isValidJson(message)) return "content must be json";

  message = JSON.parse(message);

  if (!hasDeleteFlag(message)) return "delete must be boolean";

  if (!isADeleteMessage(message)) {
    if (!hasUserName(message)) return "username must be string";
    if (!hasContent(message)) return "content must be string";
  }

  return;
}

function validate(message) {
  let error = getError(message);

  if (error)
    validatorReponse = {
      hasError: true,
      message: error,
    };
  else
    validatorReponse = {
      hasError: false,
    };

  return validatorReponse;
}

exports.validate = validate;
