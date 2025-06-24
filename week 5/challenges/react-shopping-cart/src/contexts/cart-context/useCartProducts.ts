import { useCartContext } from './CartContextProvider';
import useCartTotal from './useCartTotal';
import { ICartProduct } from 'models';

const useCartProducts = () => {
  const { products, setProducts } = useCartContext();
  const { updateCartTotal } = useCartTotal();

  const updateQuantitySafely = (
    currentProduct: ICartProduct,
    targetProduct: ICartProduct,
    quantity: number
  ): ICartProduct => {
    if (currentProduct.id === targetProduct.id) {
      return {
        ...currentProduct,
        quantity: currentProduct.quantity + quantity,
      };
    } else {
      return currentProduct;
    }
  };

  const addProduct = (newProduct: ICartProduct) => {
    const existingProductIndex = products.findIndex(
      (product) => product.id === newProduct.id
    );

    let updatedProducts;

    if (existingProductIndex !== -1) {
      updatedProducts = [...products];
      const existingProduct = updatedProducts[existingProductIndex];
      const updatedProduct = {
        ...existingProduct,
        quantity: existingProduct.quantity + newProduct.quantity,
      };
      updatedProducts[existingProductIndex] = updatedProduct;
    } else {
      updatedProducts = [...products, newProduct];
    }

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  const removeProduct = (productToRemove: ICartProduct) => {
    const updatedProducts = products.filter(
      (product: ICartProduct) => product.id !== productToRemove.id
    );

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  const increaseProductQuantity = (productToIncrease: ICartProduct) => {
    const updatedProducts = products.map((product: ICartProduct) => {
      return updateQuantitySafely(product, productToIncrease, +1);
    });

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  const decreaseProductQuantity = (productToDecrease: ICartProduct) => {
    let productIsRemoved = false;
    let updatedProducts = products.map((product) => {
      if (product.id === productToDecrease.id) {
        const newQuantity = product.quantity - 1;
        if (newQuantity <= 0) {
          productIsRemoved = true;
          return null; // Will be filtered out later
        }
        return { ...product, quantity: newQuantity };
      }
      return product;
    });

    if (productIsRemoved) {
      updatedProducts = updatedProducts.filter(
        (p): p is ICartProduct => p !== null
      );
    }

    setProducts(updatedProducts as ICartProduct[]);
    updateCartTotal(updatedProducts as ICartProduct[]);
  };

  return {
    products,
    addProduct,
    removeProduct,
    increaseProductQuantity,
    decreaseProductQuantity,
  };
};

export default useCartProducts;
