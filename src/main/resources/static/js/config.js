require.config({
    paths: {
        jquery: '../jQuery3.6.0',
        util: '../util',
        // bootstrap: 'https://cdn.staticfile.org/twitter-bootstrap/4.0.0/js/bootstrap.min',
        // bootstrap: '../../bootstrap/js/bootstrap.min',
        bootstrapBundle: 'https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min',
        // popper: 'https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min'

    },
    shim: {
        'bootstrapBundle': {
            deps: ['jquery']
        }
    }
})