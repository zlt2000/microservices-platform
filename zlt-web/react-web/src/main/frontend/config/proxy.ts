/**
 * 在生产环境 代理是无法生效的，所以这里没有生产环境的配置
 * -------------------------------
 * The agent cannot take effect in the production environment
 * so there is no configuration of the production environment
 * For details, please see
 * https://pro.ant.design/docs/deploy
 */
 export default {
  dev: {
    '/api-uaa/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
    '/api-user/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
    '/api-log/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
    '/api-search/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
    '/api-generator/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
    '/api-file/': {
      target: 'http://127.0.0.1:9900',
      changeOrigin: true,
    },
  },
  test: {
    '/api/': {
      target: 'https://proapi.azurewebsites.net',
      changeOrigin: true,
      pathRewrite: { '^': '' },
    },
  },
  pre: {
    '/api/': {
      target: 'your pre url',
      changeOrigin: true,
      pathRewrite: { '^': '' },
    },
  },
};
