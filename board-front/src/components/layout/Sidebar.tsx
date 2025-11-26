/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';
import React from 'react'

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

function Sidebar({ isOpen, onClose }: SidebarProps) {
  return (
    <aside css={sidebarStyle(isOpen)}>
      <div className="sidebar-header">
        <span>메뉴</span>
        <button className='close' onClick={onClose}>❌</button>
      </div>
      <nav className='items'>
        <a>Dashboard</a>
        <a>게시판</a>
        <a>사용자 관리</a>
        <a>설정</a>
      </nav>
    </aside>
  )
}

export default Sidebar

const sidebarStyle = (isOpen: boolean) => css`
  display: flex;
  flex-direction: column;

  width: ${isOpen ? '240px' : '0px'};
  background: white;
  min-width: 0;
  overflow: hidden;

  background: white;
  border-right: 1px solid #e5e7eb;
  
  transform: translateX(${isOpen ? "0" : "-100%"});
  transition: transform 0.25s ease;
  
  .sidebar-header {
    height: 56px;
    display: flex;
    align-items: center;
    padding: 0 1rem;
    justify-content: space-around;
    border-bottom: 1px solid #e5e7eb;
  }

  .close {
    margin-left: auto;
    border: none;
    background: none;
    font-size: 20px;
    cursor: pointer;
  }

  .items {
    display: flex;
    flex-direction: column;
    padding: 1rem;
    gap: 0.5rem;
  }

  a {
    padding: 0.75rem;
    border-radius: 8px;
    cursor: pointer;

    &:hover {
      background: var(--primary);
      color: white;
    }
  }
`;