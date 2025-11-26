/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';
import React from 'react'

interface HeaderProps {
  onToggleSidebar: () => void;
}

function Header({ onToggleSidebar }: HeaderProps) {
  return (
    <header css={headerStyle}>
      <div className="sidebar-btn" onClick={onToggleSidebar}>🍔</div>
      <h1>Board</h1>
      <div className="right">Login</div>
    </header> 
  )
}

export default Header

const headerStyle = css`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
  background: white;
  border-bottom: 1px solid #e5e7eb;

  .sidebar-btn, .right {
    cursor: pointer;
  }

  .sidebar-btn {
    font-size: 24px;
  }

  h1 {
    text-align: center;
    color: var(--primary);
  }
`;