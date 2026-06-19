import React from 'react';

export type ChipTone = 'panel' | 'blue' | 'amber' | 'red' | 'green' | 'ghost';

export interface ChipProps extends React.HTMLAttributes<HTMLSpanElement> {
  /** Pill color. @default 'panel' */
  tone?: ChipTone;
  /** Optional leading icon (e.g. a flame for a streak). */
  icon?: React.ReactNode;
}

/**
 * Small rounded pill for counts and status — the daily-streak chip, a "x3
 * COMBO" flag, a pipe-size tag. Pass `icon` for a leading glyph.
 */
export function Chip({ tone = 'panel', icon, className = '', children, ...rest }: ChipProps) {
  const cls = ['wr-chip', tone !== 'panel' ? `wr-chip--${tone}` : '', className]
    .filter(Boolean)
    .join(' ');
  return (
    <span className={cls} {...rest}>
      {icon != null && <span className="wr-chip__icon">{icon}</span>}
      {children}
    </span>
  );
}
