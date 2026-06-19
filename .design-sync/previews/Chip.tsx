import React from 'react';
import { Chip } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', gap: 14, flexWrap: 'wrap', alignItems: 'center' }}>
    {children}
  </div>
);

const Flame = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" aria-hidden="true">
    <path d="M12 2 C16 7 18 9 18 14 a6 6 0 0 1-12 0 C6 11 8 9 12 2 Z" fill="#e53935" />
    <path d="M12 9 C14 12 14.5 13 14.5 15 a2.5 2.5 0 0 1-5 0 C9.5 13.5 10.5 12.5 12 9 Z" fill="#ffc107" />
  </svg>
);

export const Tones = () => (
  <Stage>
    <Chip tone="amber" icon={<Flame />}>
      3 days
    </Chip>
    <Chip tone="green">x3 COMBO</Chip>
    <Chip tone="blue">1/2"</Chip>
    <Chip tone="red">LEAK</Chip>
    <Chip tone="ghost">Endless</Chip>
  </Stage>
);

export const Streak = () => (
  <Stage>
    <Chip tone="amber" icon={<Flame />}>
      7 day streak
    </Chip>
  </Stage>
);

export const PipeSizes = () => (
  <Stage>
    <Chip tone="blue">1/2"</Chip>
    <Chip tone="blue">3/4"</Chip>
    <Chip tone="blue">1"</Chip>
    <Chip tone="blue">1.5"</Chip>
    <Chip tone="blue">2"</Chip>
  </Stage>
);
