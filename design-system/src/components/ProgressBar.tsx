import React from 'react';

export type ProgressTone = 'blue' | 'success' | 'amber';

export interface ProgressBarProps {
  /** Fraction filled, 0..1 (clamped). */
  value: number;
  /** Fill color. @default 'blue' */
  tone?: ProgressTone;
  /** Optional label shown above the bar, left-aligned. */
  label?: React.ReactNode;
  /** Optional value text shown above the bar, right-aligned (e.g. "12 / 50"). */
  caption?: React.ReactNode;
  className?: string;
}

/**
 * Horizontal progress meter — career completion, stars toward the next rank, a
 * hold-to-join timer. Rounds the fill and animates width changes.
 */
export function ProgressBar({
  value,
  tone = 'blue',
  label,
  caption,
  className = '',
}: ProgressBarProps) {
  const pct = Math.max(0, Math.min(1, value)) * 100;
  const cls = ['wr-progress', tone !== 'blue' ? `wr-progress--${tone}` : '', className]
    .filter(Boolean)
    .join(' ');
  return (
    <div className={cls}>
      {(label != null || caption != null) && (
        <div className="wr-progress__label">
          <span>{label}</span>
          <span>{caption}</span>
        </div>
      )}
      <div
        className="wr-progress__track"
        role="progressbar"
        aria-valuenow={Math.round(pct)}
        aria-valuemin={0}
        aria-valuemax={100}
      >
        <div className="wr-progress__fill" style={{ width: `${pct}%` }} />
      </div>
    </div>
  );
}
