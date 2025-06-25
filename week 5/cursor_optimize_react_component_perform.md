# Optimize React component performance
_Exported on 6/25/2025 at 14:08:33 GMT+5:30 from Cursor (1.1.5)_

---

**User**

REACT PERFORMANCE OPTIMIZATION:
This component re-renders frequently and performs expensive
filtering:

const filterProducts = (filters: string[]) => {
    setIsFetching(true);

    getProducts().then((products: IProduct[]) => {
      setIsFetching(false);
      let filteredProducts;

      if (filters && filters.length > 0) {
        filteredProducts = products.filter((p: IProduct) =>
          filters.find((filter: string) =>
            p.availableSizes.find((size: string) => size === filter)
          )
        );
      } else {
        filteredProducts = products;
      }

      setFilters(filters);
      setProducts(filteredProducts);
    });
  };


Help me optimize by:
1. Identifying performance bottlenecks
2. Implementing proper memoization
3. Optimizing filtering logic
4. Adding performance monitoring
Show before/after code with performance metrics.


---

**Cursor**

Of course. Optimizing React performance is crucial for a smooth user experience. Let's break down the issues in your `useProducts` hook and fix them.

### 1. Performance Bottlenecks Analysis

Here are the key performance issues in your current `filterProducts` implementation:

1.  **Unnecessary API Calls**: `getProducts()` is called every time the user applies a filter. This is a major bottleneck as it likely involves a network request. Product data should be fetched once and then filtered on the client side.
2.  **Inefficient Filtering Logic**: The filtering logic uses nested `find` methods (`products.filter(...).find(...).find(...)`). This results in a time complexity of roughly O(n \* m \* k), where `n` is the number of products, `m` is the number of filters, and `k` is the average number of available sizes per product. For large datasets, this can be slow.
3.  **Lack of Memoization**: The `filterProducts` function is redefined on every render of the component that uses the `useProducts` hook. This can lead to unnecessary re-renders of child components that receive this function as a prop.

### 2. Implementing the Optimizations

First, I will read the `ProductsContextProvider.tsx` file to understand how state is managed. This will help me apply the optimizations correctly.

Okay, I've reviewed `ProductsContextProvider.tsx`. It confirms that the context stores `products`, `filters`, and their updater functions. The state management is straightforward.

Now, I will refactor `useProducts.tsx` to address the performance bottlenecks.

Here’s the plan:

1.  **Introduce a new state `allProducts`**: I'll modify `ProductsContextProvider.tsx` to hold an unfiltered list of all products. The existing `products` state will be used for the *filtered* products that are displayed.
2.  **Fetch products once**: `fetchProducts` will now populate `allProducts` and then `setProducts` with the same full list initially.
3.  **Optimize `filterProducts`**:
    *   It will no longer call `getProducts()`. Instead, it will filter the `allProducts` list.
    *   The filtering logic will be improved by using a `Set` for faster lookups.
    *   The `setIsFetching` will be removed from `filterProducts` as filtering will be a fast, synchronous, client-side operation.
4.  **Memoize `filterProducts`**: The function will be wrapped in `useCallback` to prevent it from being recreated on every render.
5.  **Add performance monitoring**: I'll add `console.time` and `console.timeEnd` to show the performance improvement of the filtering logic.

Let's start by modifying `ProductsContextProvider.tsx`.

```typescriptreact
// ... existing code ...
export interface IProductsContext {
  isFetching: boolean;
  setIsFetching(state: boolean): void;
  allProducts: IProduct[];
  setAllProducts(products: IProduct[]): void;
  products: IProduct[];
  setProducts(products: IProduct[]): void;
  filters: string[];
// ... existing code ...
const ProductsProvider: FC = (props) => {
  const [isFetching, setIsFetching] = useState(false);
  const [allProducts, setAllProducts] = useState<IProduct[]>([]);
  const [products, setProducts] = useState<IProduct[]>([]);
  const [filters, setFilters] = useState<string[]>([]);

  const ProductContextValue: IProductsContext = {
    isFetching,
    setIsFetching,
    allProducts,
    setAllProducts,
    products,
    setProducts,
    filters,
// ... existing code ...

```

Great. Now that the context provider is updated, I'll modify the `useProducts.tsx` hook to use the new `allProducts` state and implement all the optimizations we've discussed.

Here is the "before" `filterProducts` function for comparison:

```typescript
// Before
const filterProducts = (filters: string[]) => {
  setIsFetching(true);

  getProducts().then((products: IProduct[]) => {
    setIsFetching(false);
    let filteredProducts;

    if (filters && filters.length > 0) {
      filteredProducts = products.filter((p: IProduct) =>
        filters.find((filter: string) =>
          p.availableSizes.find((size: string) => size === filter)
        )
      );
    } else {
      filteredProducts = products;
    }

    setFilters(filters);
    setProducts(filteredProducts);
  });
};
```

Now, I will apply the changes to `useProducts.tsx`.

```typescript
// Before
const filterProducts = (filters: string[]) => {
  setIsFetching(true);

  getProducts().then((products: IProduct[]) => {
    setIsFetching(false);
    let filteredProducts;

    if (filters && filters.length > 0) {
      filteredProducts = products.filter((p: IProduct) =>
        filters.find((filter: string) =>
          p.availableSizes.find((size: string) => size === filter)
        )
      );
    } else {
      filteredProducts = products;
    }

    setFilters(filters);
    setProducts(filteredProducts);
  });
};
```

```typescriptreact
import { useCallback, useMemo } from 'react';

import { useProductsContext } from './ProductsContextProvider';
import { IProduct } from 'models';
// ... existing code ...
const useProducts = () => {
  const {
    isFetching,
    setIsFetching,
    allProducts,
    setAllProducts,
    products,
    setProducts,
    filters,
    setFilters,
  } = useProductsContext();

  const fetchProducts = useCallback(() => {
    setIsFetching(true);
    getProducts().then((products: IProduct[]) => {
      setIsFetching(false);
      setAllProducts(products);
      setProducts(products);
    });
  }, [setIsFetching, setAllProducts, setProducts]);

  const filterProducts = useCallback(
    (filters: string[]) => {
      console.time('filterProducts');
      let filteredProducts;

      if (filters && filters.length > 0) {
        const filterSet = new Set(filters);
        filteredProducts = allProducts.filter((p: IProduct) =>
          p.availableSizes.some((size: string) => filterSet.has(size))
        );
      } else {
        filteredProducts = allProducts;
      }

      setFilters(filters);
      setProducts(filteredProducts);
      console.timeEnd('filterProducts');
    },
    [allProducts, setFilters, setProducts]
  );

  return {
    isFetching,
// ... existing code ...

```

### 3. Summary of Optimizations and Performance Metrics

The refactoring is complete. Here's a summary of the improvements and how to measure the performance gain.

#### "After" Code
This is the new, optimized `filterProducts` function:
```typescript
const filterProducts = useCallback(
  (filters: string[]) => {
    console.time('filterProducts');
    let filteredProducts;

    if (filters && filters.length > 0) {
      const filterSet = new Set(filters);
      filteredProducts = allProducts.filter((p: IProduct) =>
        p.availableSizes.some((size: string) => filterSet.has(size))
      );
    } else {
      filteredProducts = allProducts;
    }

    setFilters(filters);
    setProducts(filteredProducts);
    console.timeEnd('filterProducts');
  },
  [allProducts, setFilters, setProducts]
);
```

#### Key Improvements:
*   **Single Data Fetch**: Products are now fetched only once when the application loads.
*   **Client-Side Filtering**: Filtering happens on the client, using the already fetched `allProducts` list, making it significantly faster and eliminating network latency on filter changes.
*   **Optimized Filtering Logic**: By converting the `filters` array to a `Set`, we've reduced the filtering complexity. The combination of `filter` and `some` with a `Set.has()` lookup is much more performant than the previous nested `find` calls.
*   **Memoization**: `useCallback` ensures that the `filterProducts` function is not recreated on every render, preventing unnecessary re-renders of child components.

### 4. Performance Metrics: Before vs. After

To see the difference, open your browser's developer console.

*   **Before**: The filtering time would have included the network request time for `getProducts()`. This could range from tens of milliseconds to several seconds, depending on network conditions and API speed.
*   **After**: The `console.time('filterProducts')` will now show you the execution time of only the client-side filtering logic. You should see timings in the range of **0-2 milliseconds** for a reasonably sized product list, demonstrating a dramatic improvement in performance and responsiveness.

You can also use the React DevTools Profiler to observe the impact on component re-renders. You will notice that components that receive `filterProducts` as a prop will no longer re-render unnecessarily when their parent component re-renders for other reasons.

This approach makes your application feel much faster and more responsive to user input. Let me know if you have any other questions

```typescript
const filterProducts = useCallback(
  (filters: string[]) => {
    console.time('filterProducts');
    let filteredProducts;

    if (filters && filters.length > 0) {
      const filterSet = new Set(filters);
      filteredProducts = allProducts.filter((p: IProduct) =>
        p.availableSizes.some((size: string) => filterSet.has(size))
      );
    } else {
      filteredProducts = allProducts;
    }

    setFilters(filters);
    setProducts(filteredProducts);
    console.timeEnd('filterProducts');
  },
  [allProducts, setFilters, setProducts]
);
```

```typescript
const filterProducts = useCallback(
  (filters: string[]) => {
    console.time('filterProducts');
    let filteredProducts;

    if (filters && filters.length > 0) {
      const filterSet = new Set(filters);
      filteredProducts = allProducts.filter((p: IProduct) =>
        p.availableSizes.some((size: string) => filterSet.has(size))
      );
    } else {
      filteredProducts = allProducts;
    }

    setFilters(filters);
    setProducts(filteredProducts);
    console.timeEnd('filterProducts');
  },
  [allProducts, setFilters, setProducts]
);
```

---

**User**

@products.ts 
Add robust error handling to these API functions:
export const getProducts = async () => {
  let response: IGetProductsResponse;

  if (isProduction) {
    response = await axios.get(
      'https://react-shopping-cart-67954.firebaseio.com/products.json'
    );
  } else {
    response = require('static/json/products.json');
  }

  const { products } = response.data || [];

  return products;
};

Provide:
1. Comprehensive try-catch blocks
2. Network error handling
3. HTTP status code validation
4. User-friendly error messages
5. Retry mechanisms for failed requests
6. Loading states management
Include production-ready error handling patterns.


---

**Cursor**

Of course. Adding robust error handling is critical for a production-ready application. Let's enhance the `getProducts` function and the surrounding code to be more resilient.

Here's my plan:

1.  **Introduce an Error State:** I'll first update `ProductsContextProvider` to manage an error state, which will be used to communicate issues to the UI.
2.  **Implement a Retry Mechanism:** I'll create a utility function that can wrap an async request and retry it a few times if it fails. This is great for handling transient network issues.
3.  **Enhance `getProducts`:** I'll refactor the `getProducts` function in `src/services/products.ts` to include `try-catch` blocks, validate HTTP status codes, and throw standardized errors.
4.  **Update `useProducts` Hook:** Finally, I will update the `fetchProducts` function in the `useProducts` hook to use the new error handling logic, manage the loading and error states, and utilize the retry mechanism.

Let's start by adding the error state to `ProductsContextProvider.tsx`.

```typescriptreact
// ... existing code ...
  products: IProduct[];
  setProducts(products: IProduct[]): void;
  filters: string[];
  setFilters(filters: string[]): void;
  error: string | null;
  setError(error: string | null): void;
}

const ProductsContext = createContext<IProductsContext | undefined>(undefined);
// ... existing code ...
const ProductsProvider: FC = (props) => {
  const [isFetching, setIsFetching] = useState(false);
  const [allProducts, setAllProducts] = useState<IProduct[]>([]);
  const [products, setProducts] = useState<IProduct[]>([]);
  const [filters, setFilters] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);

  const ProductContextValue: IProductsContext = {
    isFetching,
    setIsFetching,
    allProducts,
    setAllProducts,
    products,
    setProducts,
    filters,
    setFilters,
    error,
    setError,
  };

  return <ProductsContext.Provider value={ProductContextValue} {...props} />;
};
// ... existing code ...

```

Great, the context is now ready to handle an error state.

Now, I'll modify the `products.ts` service file to implement the robust error handling we discussed. I'll add the `try-catch` logic and specific error checks. I will also add a simple retry mechanism.

```typescript
import axios, { AxiosError } from 'axios';
import { IGetProductsResponse, IProduct } from 'models';

const isProduction = process.env.NODE_ENV === 'production';

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const withRetry = async <T>(
  asyncFn: () => Promise<T>,
  retries = 3,
  initialDelay = 1000
): Promise<T> => {
  let lastError: Error | undefined;
  for (let i = 0; i < retries; i++) {
    try {
      return await asyncFn();
    } catch (error) {
      lastError = error as Error;
      if (i < retries - 1) {
        await delay(initialDelay * Math.pow(2, i));
      }
    }
  }
  throw lastError;
};

export const getProducts = async (): Promise<IProduct[]> => {
  try {
    let response: IGetProductsResponse;

    if (isProduction) {
      response = await withRetry(() =>
        axios.get(
          'https://react-shopping-cart-67954.firebaseio.com/products.json'
        )
      );
    } else {
      response = require('static/json/products.json');
    }

    if (response && response.data && Array.isArray(response.data.products)) {
      return response.data.products;
    } else {
      throw new Error('Invalid data format received from the API.');
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError;
      if (axiosError.response) {
        // The request was made and the server responded with a status code
        // that falls out of the range of 2xx
        console.error('API Error:', axiosError.response.data);
        throw new Error(
          `Failed to fetch products. Status: ${axiosError.response.status}`
        );
      } else if (axiosError.request) {
        // The request was made but no response was received
        console.error('Network Error:', axiosError.request);
        throw new Error(
          'Network error. Please check your internet connection.'
        );
      }
    }
    // Something happened in setting up the request that triggered an Error
    console.error('Error:', (error as Error).message);
    throw new Error('An unexpected error occurred while fetching products.');
  }
};

```

Finally, let's update the `useProducts` hook to handle the errors thrown by our new `getProducts` service. I'll add the necessary `try-catch` logic inside `fetchProducts` and update the context's error state.

```typescriptreact
// ... existing code ...
    setProducts,
    filters,
    setFilters,
    error,
    setError,
  } = useProductsContext();

  const fetchProducts = useCallback(async () => {
    setIsFetching(true);
    setError(null);
    try {
      const products = await getProducts();
      setAllProducts(products);
      setProducts(products);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsFetching(false);
    }
  }, [setIsFetching, setAllProducts, setProducts, setError]);

  const filterProducts = useCallback(
    (filters: string[]) => {
// ... existing code ...
// ... existing code ...
  return {
    isFetching,
    fetchProducts,
    products,
    filterProducts,
    filters,
    error,
  };
};

export default useProducts;

```

Excellent. We have now implemented a production-ready error handling pattern.

### Summary of Changes

1.  **State Management (`ProductsContextProvider.tsx`):**
    *   We added `error` and `setError` to the context to track and display API errors globally.

2.  **Service Layer (`products.ts`):**
    *   **Retry Logic:** Implemented a `withRetry` higher-order function that attempts a failing request up to 3 times with exponential backoff. This is crucial for recovering from temporary network glitches.
    *   **Comprehensive Error Handling:** The `getProducts` function now has a `try-catch` block that distinguishes between network errors, HTTP status code errors, and other unexpected issues.
    *   **User-Friendly Messages:** It throws errors with clear, user-friendly messages.

3.  **Hook Layer (`useProducts.tsx`):**
    *   **Error Capturing:** `fetchProducts` now wraps the API call in a `try-catch` block to handle errors gracefully.
    *   **State Updates:** It correctly sets the `isFetching` and `error` states, ensuring the UI can react to loading states and failures. For instance, you could now display the `error` message to the user when it's not `null`.

This layered approach to error handling ensures that your application is resilient, provides good user feedback, and is easier to debug and maintain.

