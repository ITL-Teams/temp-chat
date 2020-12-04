const router = require("express").Router();

router.all("*", (request, response) => {
  response.status(404).send();
});

module.exports = router;
