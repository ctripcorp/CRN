const path = require('path');
const ip = require('ip');
const logOutput = require('./logOutput');
function getBundleURL(option,platform){
    var moduleName = require(path.resolve("package.json")).name;
    if (!moduleName) {
        logOutput.error("name in package.json can not be null");
        process.exit(1);
    }
    if(option.url){
        option.url = option.url.replace(/\+/g, "&");
        return option.url;
    }
    var result = [
        "http://",
        option.ip||ip.address(),
        ":",
        option.port || '8081',
        '/index.bundle',
        '?platform=',
        platform,
        "&CRNModuleName=",
        moduleName,
        "&CRNType=1"
    ].join("");
    logOutput.log(result)
    return result;
}

module.exports = getBundleURL;