require.config({
    paths: {
        jquery: '../jQuery3.6.0',
        util: '../util',
        // bootstrapBundle: 'https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min',
        bootstrapBundle: '../../bootstrap/js/bootstrap.bundle.min',

    },
    shim: {
        'bootstrapBundle': {
            deps: ['jquery']
        }
    }
})