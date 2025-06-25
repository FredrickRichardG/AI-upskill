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
