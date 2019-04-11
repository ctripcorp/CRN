const Promise = require("promise");
const packageJson= require("./../package.json");
/**
 * @description  print crn-cli version 
 */
function printVersion() {
  console.log(`${packageJson.version}`);
  return Promise.resolve();
}
module.exports=printVersion;