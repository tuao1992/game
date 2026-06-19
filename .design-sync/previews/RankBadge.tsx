import React from 'react';
import { RankBadge } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', gap: 20, flexWrap: 'wrap', alignItems: 'center' }}>
    {children}
  </div>
);

export const AllRanks = () => (
  <Stage>
    <RankBadge rank="apprentice" />
    <RankBadge rank="junior" />
    <RankBadge rank="technician" />
    <RankBadge rank="senior" />
    <RankBadge rank="expert" />
    <RankBadge rank="master" />
  </Stage>
);

export const WithLabel = () => (
  <Stage>
    <RankBadge rank="senior" size={64} showLabel />
  </Stage>
);

export const Master = () => (
  <Stage>
    <RankBadge rank="master" size={96} />
  </Stage>
);
