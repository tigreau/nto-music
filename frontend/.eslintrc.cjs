module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react-hooks/recommended',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs'],
  parser: '@typescript-eslint/parser',
  plugins: ['react-refresh'],
  rules: {
    'no-restricted-imports': [
      'error',
      {
        patterns: [
          {
            group: ['../*'],
            message: 'Use the @/ alias for cross-module imports.',
          },
        ],
      },
    ],
    'react-refresh/only-export-components': [
      'warn',
      {
        allowConstantExport: true,
        allowExportNames: ['buttonVariants', 'badgeVariants', 'useCart', 'useAuth'],
      },
    ],
  },
  overrides: [
    {
      files: ['src/components/ui/**/*.{ts,tsx}'],
      rules: {
        'no-restricted-imports': [
          'error',
          {
            patterns: [
              {
                group: ['@/api/**', '@/hooks/**', '@/pages/**', '@/context/**'],
                message: 'UI components must stay presentation-only.',
              },
            ],
          },
        ],
      },
    },
    {
      files: ['src/lib/**/*.{ts,tsx}'],
      rules: {
        'no-restricted-imports': [
          'error',
          {
            patterns: [
              {
                group: ['@/components/**', '@/pages/**', '@/context/**', '@/hooks/**', '@/api/**'],
                message: 'lib utilities must not depend on app layers.',
              },
            ],
          },
        ],
      },
    },
    {
      files: ['src/types/**/*.{ts,tsx}'],
      rules: {
        'no-restricted-imports': [
          'error',
          {
            patterns: [
              {
                group: ['@/components/**', '@/pages/**', '@/context/**', '@/hooks/**', '@/api/**', '@/lib/**'],
                message: 'types must remain framework-agnostic.',
              },
            ],
          },
        ],
      },
    },
    {
      files: ['src/api/**/*.{ts,tsx}'],
      rules: {
        'no-restricted-imports': [
          'error',
          {
            patterns: [
              {
                group: ['@/components/**', '@/pages/**', '@/context/**'],
                message: 'api clients cannot depend on UI/application layers.',
              },
            ],
          },
        ],
      },
    },
    {
      files: ['src/pages/**/*.{ts,tsx}', 'src/components/**/*.{ts,tsx}'],
      excludedFiles: ['src/components/ui/**/*.{ts,tsx}'],
      rules: {
        'no-restricted-imports': [
          'error',
          {
            paths: [
              {
                name: '@/api/client',
                message: 'Use hooks/context facades instead of calling api/client from UI layers.',
              },
            ],
          },
        ],
      },
    },
  ],
}
