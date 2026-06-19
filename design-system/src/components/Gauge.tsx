import React from 'react';

export type GaugeTone = 'blue' | 'success' | 'danger' | 'amber';

const TONE_COLOR: Record<GaugeTone, string> = {
  blue: 'var(--wr-blue-light)',
  success: 'var(--wr-green)',
  danger: 'var(--wr-red)',
  amber: 'var(--wr-amber)',
};

export interface GaugeProps {
  /** Fraction filled, 0..1 (clamped). */
  value: number;
  /** Diameter in px. @default 140 */
  size?: number;
  /** Big center text; defaults to the rounded percentage. */
  display?: React.ReactNode;
  /** Caption under the value (e.g. "PRESSURE"). */
  label?: React.ReactNode;
  /** Arc color. @default 'blue' */
  tone?: GaugeTone;
  className?: string;
}

/**
 * Radial 270° gauge used for the pressure test — a sweeping needle of color
 * over a recessed track. Green means the joint held; red means a leak.
 */
export function Gauge({ value, size = 140, display, label, tone = 'blue', className = '' }: GaugeProps) {
  const v = Math.max(0, Math.min(1, value));
  const stroke = Math.max(8, size * 0.1);
  const r = size / 2 - stroke;
  const c = 2 * Math.PI * r;
  const arc = 0.75; // 270° sweep, gap centered at the bottom
  const cls = ['wr-gauge', className].filter(Boolean).join(' ');
  return (
    <div className={cls} style={{ width: size }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <g transform={`rotate(135 ${size / 2} ${size / 2})`} fill="none" strokeLinecap="round">
          <circle
            cx={size / 2}
            cy={size / 2}
            r={r}
            stroke="rgba(0,0,0,0.32)"
            strokeWidth={stroke}
            strokeDasharray={`${arc * c} ${c}`}
          />
          <circle
            cx={size / 2}
            cy={size / 2}
            r={r}
            stroke={TONE_COLOR[tone]}
            strokeWidth={stroke}
            strokeDasharray={`${arc * v * c} ${c}`}
          />
        </g>
      </svg>
      <span className="wr-gauge__value" style={{ marginTop: -size * 0.42 }}>
        {display ?? `${Math.round(v * 100)}%`}
      </span>
      {label != null && (
        <span className="wr-gauge__label" style={{ marginTop: size * 0.2 }}>
          {label}
        </span>
      )}
    </div>
  );
}
