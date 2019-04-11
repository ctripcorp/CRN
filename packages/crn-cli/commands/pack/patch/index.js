const replacePathFiles = require('./replacePatchFiles');
const config = require('./patchconfig');

function patch() {
  replacePathFiles(config);
}
module.exports = patch;