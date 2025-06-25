import { ICartProduct } from 'models';
import CartProduct from './CartProduct';
import React, { useMemo } from 'react';

import * as S from './style';

interface IProps {
  products: ICartProduct[];
}

const CartProducts = ({ products }: IProps) => {
  const cartProductList = useMemo(
    () =>
      products?.length
        ? products.map((p) => <CartProduct product={p} key={p.sku} />)
        : (
          <S.CartProductsEmpty>
            Add some products in the cart <br /> :)
          </S.CartProductsEmpty>
        ),
    [products]
  );
  return <S.Container>{cartProductList}</S.Container>;
};

export default CartProducts;
