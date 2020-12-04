const express = require("express");

const application = express();
const expressWs = require("express-ws")(application);

// SERVER SETUP
application.set("app_name", "TempChat Server");
application.set("port", process.env.PORT || 3000);

// SERVER ROUTES
application.use(require("./routes/socket-routes")(expressWs));
application.use(require("./routes/default-routes"));

// // START APPLICATION
application.listen(application.get("port"), () => {
  console.log(
    `${application.get("app_name")} server runnig on port ${application.get(
      "port"
    )}`
  );
});
