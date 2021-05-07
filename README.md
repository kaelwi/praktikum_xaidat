# Praktikum at Xaidat GmbH
Coding Task: Speak to some API to get data, create a Caduceus event, send it to a server instance.
Caduceus Task: Build dashboards with those data, calculate some KPIs and display them as widgets.

## Coding
All backed with some testing.

**Usage:** Don't forget to give the program the path to your config file as an argument.
The config file has to have an URL and a path to your database. The intervall and filter are optional (interval is set to 1 minute by default, without a filter all fetched data is being processed).
In IntelliJ: Run - Edit Configurations - CLI arguments to your application (in my case ./src/main/resources/config.properties)

### First round
- Fetch data
- convert String (data) to Java objects (countries in this case)

### Second round
- Filter countries via a config file - Config Parser
- Get only data related to this filter - Country Mapper
- Don't send duplicate data
- Logging

### Third round
- Repeatedly fetch data after some time interval - Timer

### Fourth round
- Save data in a DB to check, if data has already been sent (check timestamp of data)
- Change getting config file from getClassPath to path via cmd argument
- Change Config Parser - config includes DB location and URL in addition
