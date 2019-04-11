
'use strict';

const fs = require('fs');
const util = require('util');
const path = require('path');
const chalk = require('chalk');

function replaceFiles(changeList) {
	changeList && changeList.forEach((item) => {
		replaceFile(item);
	});
}


function replaceFile(conf) {
  var targetFile = path.resolve(process.cwd(),conf.to);
  var modifiedFile = path.resolve(__dirname, conf.from);
  var isTargetFileExist = fs.existsSync(targetFile);
  var isModifiedFileExist = fs.existsSync(modifiedFile);
  try{
    if(isTargetFileExist && isModifiedFileExist){
      fs.unlinkSync(targetFile);
      fs.createReadStream(modifiedFile).pipe(fs.createWriteStream(targetFile));
      console.log('成功替换< ' + targetFile + ' >文件');
    }else{
      var dir = path.resolve(targetFile,'..');
      if(!fs.existsSync(dir)){
        fs.mkdirSync(dir);
      }
      fs.createReadStream(modifiedFile).pipe(fs.createWriteStream(targetFile));
      console.log('成功替换< ' + targetFile + ' >文件');
    }
  }catch(ex){
    console.error(chalk.red("replace file error"))
    console.error(ex);
  }
}

module.exports = replaceFiles;
