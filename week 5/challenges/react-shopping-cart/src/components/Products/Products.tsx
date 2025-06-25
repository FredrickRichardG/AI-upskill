import { IProduct } from 'models';
import Product from './Product';
import React, { useMemo } from 'react';

import * as S from './style';

interface IProps {
  products: IProduct[];
}

const Products = ({ products }: IProps) => {
  const productList = useMemo(
    () => products?.map((p) => <Product product={p} key={p.sku} />),
    [products]
  );
  return <S.Container>{productList}</S.Container>;
};

export default Products;
