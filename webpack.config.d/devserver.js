if(config.devServer) {
    config.devServer.port = 3002
    config.devServer.proxy = {
        context: () => true,
        target: 'http://localhost:8008',
        changeOrigin: true
    }
}