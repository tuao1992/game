import React from 'react';

export interface LogoProps {
  /** Wordmark height in px (the subtitle scales with it). @default 64 */
  size?: number;
  /** Show the "CPVC MASTER" subtitle under the wordmark. @default true */
  showSubtitle?: boolean;
  /** Accessible label. @default 'Weldrite CPVC Master' */
  'aria-label'?: string;
  className?: string;
}

/**
 * The Weldrite wordmark: "WELDRITE" in heavy white sans, underlined by a red
 * CPVC pipe that turns up at an elbow, with the "CPVC MASTER" subtitle in
 * brand light-blue. The game's primary brand lockup.
 */
export function Logo({ size = 64, showSubtitle = true, className = '', ...rest }: LogoProps) {
  const label = rest['aria-label'] ?? 'Weldrite CPVC Master';
  const cls = ['wr-logo', className].filter(Boolean).join(' ');
  const w = (size * 380) / 96;
  return (
    <span className={cls} role="img" aria-label={label}>
      <svg width={w} height={size} viewBox="0 0 380 96" aria-hidden="true">
        <text
          x="190"
          y="58"
          textAnchor="middle"
          textLength="344"
          lengthAdjust="spacingAndGlyphs"
          fontFamily="var(--wr-font)"
          fontWeight={800}
          fontSize="58"
          fill="var(--wr-white)"
        >
          WELDRITE
        </text>
        {/* Red CPVC pipe underline with an elbow turning up at the right. */}
        <path
          d="M22 76 L250 76 L288 52"
          fill="none"
          stroke="var(--wr-red)"
          strokeWidth="9"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
      {showSubtitle && (
        <span className="wr-logo__sub" style={{ fontSize: size * 0.22 }}>
          CPVC MASTER
        </span>
      )}
    </span>
  );
}
