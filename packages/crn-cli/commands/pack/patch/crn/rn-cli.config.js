/** 
 * cli配置黑名单，启动服务不检索ios目录，防止和node_modules目录文件有冲突
*/
const blacklist = require('metro-config/src/defaults/blacklist');

module.exports = {
    resolver: {
        blacklistRE: blacklist([
            /^ios\/.*/
        ])
    },
};

