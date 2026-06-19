import React from 'react';

export type Rank = 'apprentice' | 'junior' | 'technician' | 'senior' | 'expert' | 'master';

interface RankInfo {
  title: string;
  starsNeeded: number;
  color: string;
}

/** Rank metadata mirrors the game's Rank enum (title, starsNeeded, badge color). */
export const RANKS: Record<Rank, RankInfo> = {
  apprentice: { title: 'Apprentice Plumber', starsNeeded: 0, color: 'var(--wr-rank-apprentice)' },
  junior: { title: 'Junior Technician', starsNeeded: 10, color: 'var(--wr-rank-junior)' },
  technician: { title: 'Technician', starsNeeded: 25, color: 'var(--wr-rank-technician)' },
  senior: { title: 'Senior Technician', starsNeeded: 45, color: 'var(--wr-rank-senior)' },
  expert: { title: 'Plumbing Expert', starsNeeded: 75, color: 'var(--wr-rank-expert)' },
  master: { title: 'Master Plumber', starsNeeded: 110, color: 'var(--wr-rank-master)' },
};

export interface RankBadgeProps {
  /** Which rank shield to show. @default 'apprentice' */
  rank?: Rank;
  /** Shield size in px. @default 56 */
  size?: number;
  /** Show the rank title and stars-needed sub-label beside the shield. @default false */
  showLabel?: boolean;
  className?: string;
}

/**
 * Heraldic shield badge stamped with a wrench mark, tinted by rank from bronze
 * (Apprentice) up to gold (Master). The player's progression emblem.
 */
export function RankBadge({ rank = 'apprentice', size = 56, showLabel = false, className = '' }: RankBadgeProps) {
  const info = RANKS[rank];
  const cls = ['wr-rank', className].filter(Boolean).join(' ');
  return (
    <span className={cls}>
      <svg width={size} height={size} viewBox="0 0 64 72" aria-hidden="true">
        {/* Shield */}
        <path
          d="M8 6 L56 6 L56 44 Q56 60 32 70 Q8 60 8 44 Z"
          fill={info.color}
          stroke="rgba(255,255,255,0.34)"
          strokeWidth={3}
        />
        {/* Wrench mark */}
        <g stroke="var(--wr-white)" strokeWidth={5} fill="none" strokeLinecap="round">
          <line x1="22" y1="46" x2="42" y2="24" />
          <circle cx="45" cy="20" r="6.5" strokeWidth={4} />
          <circle cx="19" cy="50" r="6" strokeWidth={4} />
        </g>
      </svg>
      {showLabel && (
        <span>
          <span className="wr-rank__title">{info.title}</span>
          <br />
          <span className="wr-rank__sub">{info.starsNeeded}★ to unlock</span>
        </span>
      )}
    </span>
  );
}
