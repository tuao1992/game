import React from 'react';

export interface StarRatingProps {
  /** Number of filled stars. */
  value: number;
  /** Total stars to draw. @default 3 */
  max?: number;
  /** Size of each star in px. @default 28 */
  size?: number;
  /** Accessible label; defaults to "<value> of <max> stars". */
  'aria-label'?: string;
  className?: string;
}

/** Ten-point star path (outer R, inner 0.46R) in a 24×24 box, matching the game. */
function starPoints(): string {
  const cx = 12;
  const cy = 12;
  const outer = 11;
  const inner = outer * 0.46;
  const pts: string[] = [];
  for (let i = 0; i < 10; i++) {
    const r = i % 2 === 0 ? outer : inner;
    const a = -Math.PI / 2 + (i * Math.PI) / 5;
    pts.push(`${(cx + Math.cos(a) * r).toFixed(2)},${(cy + Math.sin(a) * r).toFixed(2)}`);
  }
  return pts.join(' ');
}

const POINTS = starPoints();

/**
 * The 1–3 star score earned for a joint, drawn as amber five-point stars.
 * Unearned stars render as hollow outlines. Also reads as a generic rating.
 */
export function StarRating({ value, max = 3, size = 28, className = '', ...rest }: StarRatingProps) {
  const filled = Math.max(0, Math.min(max, Math.round(value)));
  const label = rest['aria-label'] ?? `${filled} of ${max} stars`;
  const cls = ['wr-stars', className].filter(Boolean).join(' ');
  return (
    <span className={cls} role="img" aria-label={label}>
      {Array.from({ length: max }, (_, i) => (
        <svg key={i} width={size} height={size} viewBox="0 0 24 24" aria-hidden="true">
          <polygon
            className={i < filled ? 'wr-star--on' : 'wr-star--off'}
            points={POINTS}
            strokeWidth={i < filled ? 1.3 : 1.1}
            strokeLinejoin="round"
          />
        </svg>
      ))}
    </span>
  );
}
