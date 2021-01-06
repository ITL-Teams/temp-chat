function addZeroIfNeeded(number) {
  return number < 10 ? `0${number}` : number.toString();
}

function getTimeFormatted(date) {
  let hours = addZeroIfNeeded(date.getHours());
  let minutes = addZeroIfNeeded(date.getMinutes());

  return `${hours}:${minutes}`;
}

function getDateFormatted(date) {
  let day = addZeroIfNeeded(date.getDate());
  let month = addZeroIfNeeded(date.getMonth() + 1);
  let year = date.getFullYear().toString().slice(2);

  return `${day}/${month}/${year}`;
}

exports.getTimeFormatted = getTimeFormatted;
exports.getDateFormatted = getDateFormatted;
