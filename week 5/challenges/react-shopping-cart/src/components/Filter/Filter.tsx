import { useProducts } from 'contexts/products-context';

import * as S from './style';

export const availableSizes = ['XS', 'S', 'M', 'ML', 'L', 'XL', 'XXL'];

const Filter = () => {
  const { filters, filterProducts } = useProducts();

  const toggleCheckbox = (label: string) => {
    const selectedCheckboxes = new Set(filters);
    if (selectedCheckboxes.has(label)) {
      selectedCheckboxes.delete(label);
    } else {
      selectedCheckboxes.add(label);
    }
    filterProducts(Array.from(selectedCheckboxes));
  };

  const createCheckbox = (label: string) => (
    <S.Checkbox label={label} handleOnChange={toggleCheckbox} key={label} />
  );

  const createCheckboxes = () => availableSizes.map(createCheckbox);

  return (
    <S.Container>
      <S.Title>Sizes:</S.Title>
      {createCheckboxes()}
    </S.Container>
  );
};

export default Filter;
