'use strict';

const fs = require('fs');
const Promise = require('promise');

function writeFile(file, data, encoding) {
  return new Promise((resolve, reject) => {
    fs.writeFile(
      file,
      data,
      encoding,
      error => error ? reject(error) : resolve()
    );
  });
}

module.exports = writeFile;
