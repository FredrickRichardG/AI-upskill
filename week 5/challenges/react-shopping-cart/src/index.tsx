import { StrictMode } from 'react';
import * as ReactDOMClient from 'react-dom/client';
import * as Sentry from '@sentry/react';
import { BrowserTracing } from '@sentry/tracing';
import LogRocket from 'logrocket';
import { onCLS, onINP, onLCP } from 'web-vitals';

/* Theme */
import { ThemeProvider } from 'commons/style/styled-components';
import { theme } from 'commons/style/theme';
import GlobalStyle from 'commons/style/global-style';

/* Context Providers */
import { ProductsProvider } from 'contexts/products-context';
import { CartProvider } from 'contexts/cart-context';

import App from 'components/App';
import ErrorBoundary from 'components/ErrorBoundary/ErrorBoundary';

Sentry.init({
  dsn: 'https://<your-dsn>@sentry.io/<project-id>', // Replace with your Sentry DSN
  integrations: [new BrowserTracing()],
  tracesSampleRate: 1.0,
});

LogRocket.init('your-app-id'); // Replace with your LogRocket app ID

onCLS(console.log);
onINP(console.log);
onLCP(console.log);

const root = document.getElementById('root')!;
const container = ReactDOMClient.createRoot(root);

container.render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <ErrorBoundary>
        <ProductsProvider>
          <CartProvider>
            <App />
          </CartProvider>
        </ProductsProvider>
      </ErrorBoundary>
    </ThemeProvider>
  </StrictMode>
);
