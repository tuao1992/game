import React from 'react';

export type TaglineVariant = 'primary' | 'secondary';

const SLOGANS: Record<TaglineVariant, string> = {
  primary: 'Strong Bond. Zero Leaks.',
  secondary: 'Built To Last.',
};

export interface TaglineProps {
  /**
   * Which brand slogan to show. `primary` = "Strong Bond. Zero Leaks.",
   * `secondary` = "Built To Last." @default 'primary'
   */
  variant?: TaglineVariant;
  /** Emphasize in white instead of the brand light-blue. @default false */
  strong?: boolean;
  /** Override the slogan text. */
  children?: React.ReactNode;
  className?: string;
}

/**
 * The Weldrite brand tagline, set in bold sans. Use it under the logo on menus
 * and result screens to reinforce the "Strong Bond. Zero Leaks." promise.
 */
export function Tagline({ variant = 'primary', strong = false, children, className = '' }: TaglineProps) {
  const cls = ['wr-tagline', strong ? 'wr-tagline--strong' : '', className].filter(Boolean).join(' ');
  return <span className={cls}>{children ?? SLOGANS[variant]}</span>;
}
