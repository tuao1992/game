import React from 'react';

export interface CementCanProps {
  /** Can height in px. @default 150 */
  size?: number;
  /** Draw a soft light-blue glow behind the can (hero/menu prop). @default false */
  glow?: boolean;
  /** Accessible label. @default 'Weldrite CPVC solvent cement can' */
  'aria-label'?: string;
  className?: string;
}

/**
 * The Weldrite solvent-cement can — the game's signature prop. A blue can with
 * a white label band edged in red stripes, "WELDRITE / CPVC CEMENT" lettering
 * and a red cap. Drawn as vector art so it stays crisp at any size.
 */
export function CementCan({ size = 150, glow = false, className = '', ...rest }: CementCanProps) {
  const label = rest['aria-label'] ?? 'Weldrite CPVC solvent cement can';
  const cls = ['wr-can', className].filter(Boolean).join(' ');
  const w = (size * 180) / 240;
  return (
    <svg
      className={cls}
      width={w}
      height={size}
      viewBox="0 0 180 240"
      role="img"
      aria-label={label}
    >
      {glow && <ellipse cx="90" cy="134" rx="94" ry="100" fill="rgba(66,165,245,0.18)" />}
      {/* Cap */}
      <rect x="60" y="8" width="60" height="34" rx="8" fill="var(--wr-red)" />
      <rect x="64" y="12" width="52" height="8" rx="4" fill="rgba(255,255,255,0.33)" />
      {/* Body */}
      <rect x="24" y="36" width="132" height="192" rx="16" fill="var(--wr-blue)" />
      <rect x="36" y="46" width="16" height="172" rx="6" fill="rgba(255,255,255,0.22)" />
      {/* White label band with red stripes */}
      <rect x="24" y="96" width="132" height="76" fill="var(--wr-white)" />
      <rect x="24" y="96" width="132" height="9" fill="var(--wr-red)" />
      <rect x="24" y="163" width="132" height="9" fill="var(--wr-red)" />
      {/* Brand lettering */}
      <text
        x="90"
        y="134"
        textAnchor="middle"
        fontFamily="var(--wr-font)"
        fontWeight={800}
        fontSize="24"
        fill="var(--wr-blue-dark)"
      >
        WELDRITE
      </text>
      <text
        x="90"
        y="154"
        textAnchor="middle"
        fontFamily="var(--wr-font)"
        fontWeight={800}
        fontSize="12"
        letterSpacing="1"
        fill="var(--wr-red)"
      >
        CPVC CEMENT
      </text>
    </svg>
  );
}
