const path = require('path');
const chalk = require('chalk');
const writeFile = require("../../tools/writeFile");
const logOutput = require("../../tools/logOutput");
const fs = require('fs');
const Promise = require("promise");
const spawnSync = require("child_process").spawnSync;
const getBundleURL = require('../../tools/getBundleURL');
const server = require('../server/start');
const printHelp = require('../../tools/printHelp');

const getCliPath = function () {
    return path.resolve(process.cwd(), 'node_modules', 'react-native', 'cli.js');
};

function runAndroid() {
    return new Promise((resolve, reject) => {
        let args = process.argv.slice(2);
        process.argv.push('--no-packager'); // 设置react-native run-andorid不启动服务
        if (args[1] == '--help' || args[1] == '-h' || args[0] == '-H') {
            printHelp('run-android');
            return;
        }
        let cli;
        const cliPath = getCliPath();
        if (fs.existsSync(cliPath)) {
            cli = require(cliPath);
        }
        if (cli) {
            var options = require('minimist')(process.argv.slice(2));
            if(options['url']){
                process.argv.splice(process.argv.indexOf('--url'),1); 
            }
            writeFile(".__RN_Debug_URL.log", getBundleURL(options,"android"), "utf8").then(() => {
                spawnSync('adb', ['push', '.__RN_Debug_URL.log', '/sdcard/'], { stdio: "inherit" });
                server().then(() => {
                    setTimeout(() => {
                        cli.run();
                        resolve();
                    }, 100)
                });
            })
        } else {
            console.error(chalk.red(cliPath + ' is not exist'));
            reject();
        }
    })
}

module.exports = runAndroid;