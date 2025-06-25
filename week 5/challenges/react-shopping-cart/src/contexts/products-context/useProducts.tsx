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
    fetchProducts,
    products,
    filterProducts,
    filters,
  };
};

export default useProducts;
