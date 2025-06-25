/**
 * Formats a price for display.
 * @param {number} price - The price value.
 * @param {string} currencyId - The currency code (e.g., 'USD').
 * @returns {string} - Formatted price string.
 * @example
 * formatPrice(10.9, 'USD'); // "10.90"
 */
const formatPrice = (price: number, currencyId: string): string => {
  switch (currencyId) {
    case 'BRL':
      return price.toFixed(2).replace('.', ',');
    default:
      return price.toFixed(2);
  }
};

export default formatPrice;
