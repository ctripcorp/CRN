'use strict';
const chalk = require('chalk');
function error() {
	const message = Array.prototype.slice.call(arguments).join(' ');
	if (message) {
		console.log(chalk.red("[crn-cli error]: " + message));
	}
}

function warn() {
	const message = Array.prototype.slice.call(arguments).join(' ');
	if (message) {
		console.log(chalk.yellow("[crn-cli warn]: " + message));
	}
}

function log() {
	const message = Array.prototype.slice.call(arguments).join(' ');
	if (message) {
		console.log("[crn-cli]: " + message);
	}
}

module.exports = {
	error: error,
	warn: warn,
	log: log,
};