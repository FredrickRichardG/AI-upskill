import { useCallback, useMemo } from 'react';

import { useProductsContext } from './ProductsContextProvider';
import { IProduct } from 'models';
import { getProducts } from 'services/products';

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
    fetchProducts,
    products,
    filterProducts,
    filters,
    error,
  };
};

export default useProducts;
