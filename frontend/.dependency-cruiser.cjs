/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
  forbidden: [
    {
      name: 'no-circular',
      severity: 'error',
      from: {},
      to: {
        circular: true,
      },
    },
    {
      name: 'types-isolated',
      severity: 'error',
      from: {
        path: '^src/types/',
      },
      to: {
        path: '^src/',
        pathNot: '^src/types/',
      },
    },
    {
      name: 'lib-no-app-layer-deps',
      severity: 'error',
      from: {
        path: '^src/lib/',
      },
      to: {
        path: '^src/(api|hooks|context|components|pages)/',
      },
    },
    {
      name: 'api-no-ui-deps',
      severity: 'error',
      from: {
        path: '^src/api/',
      },
      to: {
        path: '^src/(components|pages|context)/',
      },
    },
    {
      name: 'hooks-no-ui-deps',
      severity: 'error',
      from: {
        path: '^src/hooks/',
      },
      to: {
        path: '^src/(components|pages)/',
      },
    },
    {
      name: 'ui-no-direct-api-client',
      severity: 'error',
      from: {
        path: '^src/(pages|components)/',
      },
      to: {
        path: '^src/api/client\\.ts$',
      },
    },
    {
      name: 'ui-presentation-only',
      severity: 'error',
      from: {
        path: '^src/components/ui/',
      },
      to: {
        path: '^src/(api|hooks|context|pages|components/(?!ui/))',
      },
    },
  ],
  options: {
    doNotFollow: {
      path: 'node_modules',
    },
    includeOnly: '^src',
    tsConfig: {
      fileName: 'tsconfig.json',
    },
    enhancedResolveOptions: {
      extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
    },
    reporterOptions: {
      dot: {
        collapsePattern: 'node_modules/[^/]+',
      },
    },
  },
};
