import React from 'react';

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'success' | 'ghost';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** Visual style. Mirrors the game's BtnStyle. @default 'primary' */
  variant?: ButtonVariant;
  /** Control height and padding. @default 'md' */
  size?: ButtonSize;
  /** Stretch to fill the container width. @default false */
  fullWidth?: boolean;
}

/**
 * Primary call-to-action button in the Weldrite style — bold sans label, a
 * thick tinted border and the signature chunky bottom shadow that depresses on
 * press. The five variants map to the game's actions: `primary` (Play),
 * `secondary`, `danger` (Exit), `success` (Confirm) and `ghost` (low emphasis).
 */
export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { variant = 'primary', size = 'md', fullWidth = false, className = '', children, type = 'button', ...rest },
  ref,
) {
  const cls = [
    'wr-btn',
    `wr-btn--${variant}`,
    `wr-btn--${size}`,
    fullWidth ? 'wr-btn--full' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');
  return (
    <button ref={ref} type={type} className={cls} {...rest}>
      {children}
    </button>
  );
});
